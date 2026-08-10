package ir.speakup.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * قرارداد API — آینه اندپوینت‌های `backend/src/index.ts`.
 * هر تغییری اینجا باید همزمان آنجا هم اعمال شود.
 */
interface Api {

    @GET("v1/time")
    suspend fun serverTime(): Response<TimeResponse>

    @POST("v1/auth/request-code")
    suspend fun requestCode(@Body body: PhoneRequest): Response<RequestCodeResponse>

    @POST("v1/auth/verify")
    suspend fun verify(@Body body: VerifyRequest): Response<VerifyResponse>

    @GET("v1/me")
    suspend fun me(): Response<MeResponse>

    @GET("v1/league")
    suspend fun league(): Response<LeagueResponse>

    @GET("v1/streak")
    suspend fun streak(): Response<StreakResponse>

    @POST("v1/streak/check-in")
    suspend fun checkIn(): Response<CheckInResponse>

    @POST("v1/sync")
    suspend fun sync(@Body body: SyncRequest): Response<SyncResponse>

    @GET("v1/plans")
    suspend fun plans(): Response<PlansResponse>

    /** فقط توکن خرید می‌رود؛ اعتبارسنجی و تصمیم، سمت سرور است */
    @POST("v1/purchase/verify")
    suspend fun verifyPurchase(@Body body: PurchaseRequest): Response<PurchaseResponse>

    @GET("v1/content/manifest")
    suspend fun contentManifest(): Response<ManifestResponse>

    /** بدنه خام برمی‌گردد تا پیش از تجزیه بتوان اعتبارش را سنجید */
    @GET("v1/content/{level}")
    suspend fun contentBundle(@Path("level") level: String): Response<okhttp3.ResponseBody>
}

@Serializable
data class ManifestResponse(
    val bundles: List<BundleInfo> = emptyList(),
    val imagesBaseUrl: String = "/images/",
)

@Serializable
data class BundleInfo(
    val level: String,
    val version: String,
    val bytes: Long = 0,
    val updatedAt: Long = 0,
)

// ---------------------------------------------------------------- زمان

@Serializable
data class TimeResponse(
    val epochSeconds: Long,
    val today: String,
    val timezone: String,
)

// ---------------------------------------------------------------- احراز هویت

@Serializable data class PhoneRequest(val phone: String)

@Serializable
data class RequestCodeResponse(
    val ok: Boolean = true,
    val expiresIn: Int = 120,
    val codeLength: Int = 5,
    /** فقط در سرور توسعه پر می‌شود؛ در تولید هرگز نمی‌آید */
    val devCode: String? = null,
    /**
     * حساب تست — سرور گفته این شماره کد نمی‌خواهد.
     *
     * سرور در حالت production اجازه نمی‌دهد چنین شماره‌ای تعریف شود، پس
     * این مسیر فقط روی سرور توسعه فعال می‌شود.
     */
    val skipCode: Boolean = false,
)

@Serializable data class VerifyRequest(val phone: String, val code: String)

@Serializable
data class VerifyResponse(val token: String, val user: RemoteUser)

@Serializable
data class RemoteUser(
    val phone: String,
    val referralCode: String,
    val isNew: Boolean = false,
)

@Serializable
data class MeResponse(
    val phone: String,
    val displayName: String? = null,
    val referralCode: String,
    val currentLevel: String = "A1",
    val subscription: RemoteSubscription? = null,
)

@Serializable
data class RemoteSubscription(val plan: String, val expiresAt: Long? = null)

// ---------------------------------------------------------------- خرید

@Serializable data class PlansResponse(val plans: List<RemotePlan> = emptyList())

@Serializable
data class RemotePlan(
    val code: String,
    val sku: String,
    val title: String,
    /** مدت بر حسب روز — طرح هفتگی در ماه نمی‌گنجد */
    val days: Int,
    val priceRial: Long,
    val badge: String? = null,
    val note: String? = null,
)

@Serializable data class PurchaseRequest(val sku: String, val purchaseToken: String)

@Serializable
data class PurchaseResponse(
    val plan: String,
    val expiresAt: Long? = null,
    val alreadyRedeemed: Boolean = false,
)

@Serializable
data class ApiError(val error: String, val retryAfter: Int? = null)

// ---------------------------------------------------------------- زنجیره

@Serializable
data class StreakResponse(
    val currentLength: Int = 0,
    val longestLength: Int = 0,
    val lastActiveDate: String? = null,
    val freezeCount: Int = 0,
    val today: String,
)

@Serializable
data class CheckInResponse(
    val result: String,
    val currentLength: Int = 0,
    val longestLength: Int = 0,
    val lastActiveDate: String? = null,
    val freezeCount: Int = 0,
    val today: String,
)

// ---------------------------------------------------------------- همگام‌سازی

@Serializable
data class SyncRequest(
    val since: Long = 0,
    val progress: List<RemoteProgress> = emptyList(),
    val leitner: List<RemoteLeitner> = emptyList(),
    val answers: List<RemoteAnswer> = emptyList(),
)

@Serializable
data class SyncResponse(
    val now: Long,
    val progress: List<RemoteProgress> = emptyList(),
    val leitner: List<RemoteLeitner> = emptyList(),
    val applied: AppliedStats? = null,
    val streak: StreakSummary? = null,
)

@Serializable
data class AppliedStats(
    val progressApplied: Int = 0,
    val progressRejected: Int = 0,
    val leitnerApplied: Int = 0,
    val leitnerRejected: Int = 0,
    val answersStored: Int = 0,
)

@Serializable
data class StreakSummary(
    val currentLength: Int = 0,
    val longestLength: Int = 0,
    val lastActiveDate: String? = null,
    val freezeCount: Int = 0,
)

@Serializable
data class RemoteProgress(
    val activityId: String,
    val status: String,
    val score: Float? = null,
    val lastItem: Int = 0,
    val completedAt: Long? = null,
    val updatedAt: Long,
)

@Serializable
data class RemoteLeitner(
    @SerialName("entryId") val entryId: String,
    val word: String,
    val box: Int,
    val dueAt: Long,
    val correctStreak: Int = 0,
    val totalReviews: Int = 0,
    val source: String,
    val updatedAt: Long,
)

@Serializable
data class RemoteAnswer(
    val itemId: String,
    val activityId: String,
    val isProductive: Boolean,
    val userAnswer: String,
    val isCorrect: Boolean,
    val targetWord: String? = null,
    val answeredAt: Long,
)

@Serializable
data class LeagueResponse(
    val tier: Int = 0,
    val tierName: String = "",
    val cohort: Int = 1,
    val weekStart: String = "",
    val rows: List<LeagueRow> = emptyList(),
)

@Serializable
data class LeagueRow(
    val rank: Int = 0,
    val userId: Int = 0,
    val name: String = "",
    val xp: Int = 0,
    val isMe: Boolean = false,
    val isDemo: Boolean = false,
)
