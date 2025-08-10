package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PromptEnum {
    GEN_NEW_FEED("Based on the following news summaries, generate two English vocabulary words for each topic, along with:\\n\\nThe meaning or usage of each word,\\n\\nA sample sentence for each word,\\nand provide Vietnamese translations for both meaning and sentence.\\n\\n\uD83D\uDD14 Important: The output must strictly follow this format:\\ntopic1-word1-eng: <English word>\\ntopic1-word1-vn: <Vietnamese meaning>\\ntopic1-usage1-eng: <English usage/definition>\\ntopic1-usage1-vn: <Vietnamese usage/definition>\\ntopic1-sentence1-eng: <Sample sentence in English>\\ntopic1-sentence1-vn: <Translated sentence in Vietnamese>\\nThen continue similarly for:\\n\\ntopic1-word2\\n\\ntopic2-word1, topic2-word2, etc."),
    GEN_DESCRIPTION("You will be given:\\n- word: <replaced>\\n\\nTask:\\n1. Write a simple, clear, beginner-friendly definition that accurately describes ONLY the given word (<replaced>).\\n2. Create three wrong options (distractors) that do NOT match the meaning of the given word but are still plausible choices for a quiz.\\n\\nOutput exactly in this format:\\n- description: <definition of the given word>\\n- option1: <wrong option>\\n- option2: <wrong option>\\n- option3: <wrong option>\\n\\nRules:\\n- The description must match the meaning of the given word and nothing else.\\n- Wrong options must be unrelated in meaning to the given word.\\n- Wrong options must NOT be synonyms or translations of the given word.\\n- Do NOT include the given word in any wrong option.\\n- Each option should be a single word or short phrase (≤3 words).\\n- Keep the same part of speech as the given word.\\n- Avoid obscure or rare words.\\n- Do not add explanations, checkmarks, or numbering — only the description line and three option lines.\\n\\nExample input:\\nword: hello\\n\\nExample output:\\ndescription: A common greeting used when meeting someone or starting a conversation.\\noption1: goodbye\\noption2: please\\noption3: thank you\\n");

    private String value;
}
