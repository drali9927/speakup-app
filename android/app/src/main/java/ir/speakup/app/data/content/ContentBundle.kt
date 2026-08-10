package ir.speakup.app.data.content

import kotlinx.serialization.Serializable

/**
 * قرارداد بسته محتوا — دقیقاً همان چیزی که content/export_to_app.py تولید می‌کند.
 * تغییر اینجا باید همزمان در آن اسکریپت هم اعمال شود.
 */
@Serializable
data class ContentBundle(
    val version: Int,
    val levelCode: String,
    val levels: List<LevelDto> = emptyList(),
    val lessons: List<LessonDto> = emptyList(),
    val sections: List<SectionDto> = emptyList(),
    val activities: List<ActivityDto> = emptyList(),
    val items: List<ItemDto> = emptyList(),
    val dictionary: List<DictionaryDto> = emptyList(),
)

@Serializable
data class DictionaryDto(
    val id: String,
    val word: String,
    val lemma: String,
    val pos: String,
    val ipaUk: String? = null,
    val ipaUs: String? = null,
    val definitionEn: String? = null,
    val translationFa: String,
    val exampleEn: String? = null,
    val exampleFa: String? = null,
    val frequencyRank: Int = 0,
)

@Serializable
data class LevelDto(
    val code: String,
    val titleFa: String,
    val titleEn: String,
    val sortOrder: Int,
)

@Serializable
data class LessonDto(
    val id: String,
    val levelCode: String,
    val number: Int,
    val titleEn: String,
    val grammarTopicFa: String,
    val themeFa: String,
    val isFree: Boolean,
    val estimatedMinutes: Int,
    val colorHex: String,
)

@Serializable
data class SectionDto(
    val id: String,
    val lessonId: String,
    val type: String,
    val sortOrder: Int,
    val estimatedMinutes: Int,
)

@Serializable
data class ActivityDto(
    val id: String,
    val sectionId: String,
    val title: String,
    val descriptionFa: String,
    val activityType: String,
    val sortOrder: Int,
)

@Serializable
data class ItemDto(
    val id: String,
    val activityId: String,
    val sortOrder: Int,
    val prompt: String,
    val promptFa: String? = null,
    val exampleEn: String? = null,
    val exampleFa: String? = null,
    val correctAnswer: String? = null,
    val alternatives: String? = null,
    val options: String? = null,
    val hintFa: String? = null,
    val imageFile: String? = null,
    val ttsText: String? = null,
    val targetWord: String? = null,
    val checkPrompt: String? = null,
    val checkAnswer: String? = null,
    val checkOptions: String? = null,
    val checkTips: String? = null,
)
