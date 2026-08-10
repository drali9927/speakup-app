import assert from 'node:assert/strict'
import { test, describe } from 'node:test'
import { createSmsSender, kavenegarSender, nullSender } from '../src/lib/sms.js'

const OK = { return: { status: 200, message: 'ok' } }

function fakeFetch(response: unknown, init: { status?: number } = {}) {
  const calls: string[] = []
  const impl = (async (url: string) => {
    calls.push(String(url))
    return {
      ok: (init.status ?? 200) < 400,
      status: init.status ?? 200,
      json: async () => response,
    } as Response
  }) as unknown as typeof fetch
  return { impl, calls }
}

describe('kavenegarSender', () => {
  test('sends code to the verify/lookup endpoint', async () => {
    const { impl, calls } = fakeFetch(OK)
    const sender = kavenegarSender({ apiKey: 'KEY', template: 'speakup-otp', fetchImpl: impl })

    assert.deepEqual(await sender.send('09123456789', '12345'), { ok: true })
    assert.equal(calls.length, 1)
    assert.match(calls[0], /verify\/lookup\.json/)
    assert.match(calls[0], /receptor=09123456789/)
    assert.match(calls[0], /token=12345/)
    assert.match(calls[0], /template=speakup-otp/)
  })

  test('treats a non-200 body status as failure even when HTTP is 200', async () => {
    // کاوه‌نگار روی اعتبار تمام‌شده هم HTTP 200 می‌دهد؛ اگر فقط به res.ok
    // نگاه کنیم، کاربر «کد فرستاده شد» می‌بیند و هیچ پیامکی نمی‌آید.
    const { impl } = fakeFetch({ return: { status: 418, message: 'credit' } })
    const sender = kavenegarSender({ apiKey: 'K', template: 't', fetchImpl: impl })

    assert.deepEqual(await sender.send('09123456789', '12345'), {
      ok: false,
      error: 'kavenegar_418',
    })
  })

  test('reports failure when the body cannot be parsed', async () => {
    const impl = (async () =>
      ({ ok: true, status: 200, json: async () => { throw new Error('not json') } }) as unknown as Response
    ) as unknown as typeof fetch
    const sender = kavenegarSender({ apiKey: 'K', template: 't', fetchImpl: impl })

    assert.equal((await sender.send('09123456789', '12345')).ok, false)
  })

  test('gives up rather than hanging on an unresponsive provider', async () => {
    const impl = ((_url: string, opts: { signal: AbortSignal }) =>
      new Promise((_resolve, reject) => {
        opts.signal.addEventListener('abort', () => {
          const e = new Error('aborted')
          e.name = 'AbortError'
          reject(e)
        })
      })) as unknown as typeof fetch
    const sender = kavenegarSender({ apiKey: 'K', template: 't', fetchImpl: impl, timeoutMs: 20 })

    assert.deepEqual(await sender.send('09123456789', '12345'), {
      ok: false,
      error: 'sms_timeout',
    })
  })

  test('reports failure when the provider is unreachable', async () => {
    const impl = (async () => { throw new Error('ENOTFOUND') }) as unknown as typeof fetch
    const sender = kavenegarSender({ apiKey: 'K', template: 't', fetchImpl: impl })

    assert.deepEqual(await sender.send('09123456789', '12345'), {
      ok: false,
      error: 'sms_unreachable',
    })
  })
})

describe('createSmsSender', () => {
  test('an unconfigured provider fails loudly instead of pretending to send', async () => {
    const sender = createSmsSender({ provider: 'none', kavenegarApiKey: '', kavenegarTemplate: '' })
    assert.equal(sender.name, nullSender.name)
    assert.equal((await sender.send('09123456789', '12345')).ok, false)
  })

  test('selects the requested provider', () => {
    assert.equal(
      createSmsSender({ provider: 'kavenegar', kavenegarApiKey: 'k', kavenegarTemplate: 't' }).name,
      'kavenegar',
    )
    assert.equal(
      createSmsSender({ provider: 'console', kavenegarApiKey: '', kavenegarTemplate: '' }).name,
      'console',
    )
  })
})
