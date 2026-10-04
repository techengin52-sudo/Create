package com.example.service

import com.example.model.VideoStyle

object PromptEnhancer {

    private val samplePrompts = listOf(
        "A stealth cyber-ninja dashing across rain-slicked Tokyo rooftops illuminated by holographic dragon billboards, anamorphic lens, 8k",
        "Majestic eagle diving down a snow-capped alpine valley in extreme slow motion, crystal clear sunlight shining through mist",
        "Ancient Roman legionnaires marching through a misty morning valley during sunrise, cinematic dust particles and golden flare",
        "A mystical glowing crystalline owl perched on a giant bioluminescent mushroom in a deep enchanted twilight forest",
        "Close up portrait of an astronaut floating in deep space looking back at planet Earth reflecting in the golden visor, photorealistic",
        "A vintage 1960s steam locomotive roaring across a wooden mountain bridge engulfed in warm autumn foliage, 35mm film grain"
    )

    fun getRandomPrompt(): String = samplePrompts.random()

    fun enhancePrompt(original: String, style: VideoStyle): String {
        val trimmed = original.trim()
        if (trimmed.isEmpty()) return ""

        val modifier = when (style) {
            VideoStyle.CINEMATIC -> "shot on 35mm anamorphic lens, volumetric lighting, subtle film grain, dramatic shallow depth of field, 8k cinematic masterpiece"
            VideoStyle.REALISTIC -> "photorealistic 8k, natural daylight, hyper-detailed texture, accurate fluid dynamics, smooth documentary camera motion"
            VideoStyle.HISTORICAL -> "historical period accuracy, warm golden hour palette, atmospheric dust motes, epic cinematic scale, cinematic grade"
            VideoStyle.FANTASY -> "magical bioluminescence, ethereal haze, mystical particle glow, surreal lighting, otherworldly color depth"
        }

        return if (trimmed.contains(modifier, ignoreCase = true)) {
            trimmed
        } else {
            "$trimmed, $modifier"
        }
    }
}
