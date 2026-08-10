#!/bin/bash
# ui.sh            -> dump current screen elements
# ui.sh tap <id>   -> tap element by resource-id substring or text
ADB=/Users/alikavyani/Library/Android/sdk/platform-tools/adb
$ADB shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1
$ADB shell cat /sdcard/ui.xml > /tmp/ui_cur.xml 2>/dev/null
python3 - "$1" "$2" <<'PY'
import sys,re
x=open('/tmp/ui_cur.xml',encoding='utf-8',errors='ignore').read()
els=[]
for m in re.finditer(r'<node[^>]*>',x):
    n=m.group(0)
    g=lambda k:(re.search(k+r'="([^"]*)"',n).group(1) if re.search(k+r'="([^"]*)"',n) else '')
    b=re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"',n)
    if not b: continue
    x1,y1,x2,y2=map(int,b.groups())
    els.append(dict(t=g('text'),d=g('content-desc'),r=g('resource-id').split('/')[-1],
                    c=g('class').split('.')[-1],k=g('clickable'),cx=(x1+x2)//2,cy=(y1+y2)//2))
mode=sys.argv[1] if len(sys.argv)>1 else ''
if mode=='tap':
    q=sys.argv[2]
    for e in els:
        if q.lower() in e['r'].lower() or q in e['t'] or q in e['d']:
            print(f"TAP {e['cx']},{e['cy']} :: {e['r']} '{e['t'] or e['d']}'"); print('COORD',e['cx'],e['cy']); break
    else: print('NOT FOUND',q)
else:
    for e in els:
        if e['t'] or e['d'] or e['k']=='true':
            print(f"{e['cx']:5},{e['cy']:5} {'[C]' if e['k']=='true' else '   '} {e['c'][:14]:15} {e['r'][:28]:29} {(e['t'] or e['d'])[:60]}")
PY
