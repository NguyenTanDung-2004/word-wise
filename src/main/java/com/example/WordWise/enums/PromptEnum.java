package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PromptEnum {
    GEN_NEW_FEED("Based on the following news summaries, generate two English vocabulary words for each topic, along with:\\n\\nThe meaning or usage of each word,\\n\\nA sample sentence for each word,\\nand provide Vietnamese translations for both meaning and sentence.\\n\\n\uD83D\uDD14 Important: The output must strictly follow this format:\\ntopic1-word1-eng: <English word>\\ntopic1-word1-vn: <Vietnamese meaning>\\ntopic1-usage1-eng: <English usage/definition>\\ntopic1-usage1-vn: <Vietnamese usage/definition>\\ntopic1-sentence1-eng: <Sample sentence in English>\\ntopic1-sentence1-vn: <Translated sentence in Vietnamese>\\nThen continue similarly for:\\n\\ntopic1-word2\\n\\ntopic2-word1, topic2-word2, etc."),
    GEN_DESCRIPTION("You will be given:\\n- word: <replaced>\\n\\nTask:\\n1. Write a simple, clear, beginner-friendly definition that accurately describes ONLY the given word (<replaced>).\\n2. Create three wrong options (distractors) that do NOT match the meaning of the given word but are still plausible choices for a quiz.\\n\\nOutput exactly in this format:\\n- description: <definition of the given word>\\n- option1: <wrong option>\\n- option2: <wrong option>\\n- option3: <wrong option>\\n\\nRules:\\n- The description must match the meaning of the given word and nothing else.\\n- Wrong options must be unrelated in meaning to the given word.\\n- Wrong options must NOT be synonyms or translations of the given word.\\n- Do NOT include the given word in any wrong option.\\n- Each option should be a single word or short phrase (≤3 words).\\n- Keep the same part of speech as the given word.\\n- Avoid obscure or rare words.\\n- Do not add explanations, checkmarks, or numbering — only the description line and three option lines.\\n\\nExample input:\\nword: hello\\n\\nExample output:\\ndescription: A common greeting used when meeting someone or starting a conversation.\\noption1: goodbye\\noption2: please\\noption3: thank you\\n"),
    ADD_WORD_AUTO("You will receive an input in the format:\n\"<userId>, <description>\"\nConvert it into a JSON object with the following structure:\n{\n  \"userId\": \"<userId from input>\",\n  \"context\": \"<context from description>\",\n  \"isExtension\": false,\n  \"englishWord\": \"<english word from description>\",\n  \"vietnameseWord\": \"<translate englishWord to vietnamese>\",\n  \"note\": \"<note from description>\"\n}\n- Always set \"isExtension\" to false.\n- Extract \"context\", \"englishWord\", \"vietnameseWord\", and \"note\" from the description naturally.\n- Keep the field names exactly as given.\n- Output valid JSON only, no extra explanation.\n\nExample input:\n123, Add the word \"apple\" which means \"quả táo\" in Vietnamese. Context: fruit vocabulary. Note: common fruit.\n\nExpected output:\n{\n  \"userId\": \"123\",\n  \"context\": \"fruit vocabulary\",\n  \"isExtension\": false,\n  \"englishWord\": \"apple\",\n  \"vietnameseWord\": \"quả táo\",\n  \"note\": \"common fruit\"\n}"),
    GEN_IDIOM(
            "You are an English language expert.\n" +
                    "I will give you a single word.\n" +
                    "Your task is to check if this word appears in any common English idiom.\n\n" +
                    "⚠️ IMPORTANT: You must return ONLY valid JSON. Do not add any explanations, comments, or extra text.\n\n" +
                    "Input: <word>\n\n" +
                    "Output format (strict):\n" +
                    "- If the word is part of a common idiom, return:\n" +
                    "{\n" +
                    "  \"idiom\": \"the idiom here\",\n" +
                    "  \"vietnamese\": \"the meaning in Vietnamese\"\n" +
                    "}\n\n" +
                    "- If no idiom contains this word, return:\n" +
                    "{\n" +
                    "  \"idiom\": \"\"\n" +
                    "}\n"
    ),
    GEN_QUESTIONS_PRACTICE_ROOM("You are a quiz question generator for a learning application.\\r\\n\\r\\nOutput must be a JSON array ONLY (no prose, no comments). Each item must strictly match this schema:\\r\\n\\r\\n{\\r\\n  \\\"type\\\": \\\"\\\",       \\/\\/ TRANSLATE, SELECT, or COMPLETE\\r\\n  \\\"question\\\": \\\"\\\",\\r\\n  \\\"anwser\\\": \\\"\\\",\\r\\n  \\\"options\\\": [\\r\\n    { \\\"value\\\": \\\"\\\", \\\"isTrue\\\": true\\/false }\\r\\n  ]\\r\\n}\\r\\n\\r\\nTYPES & RULES\\r\\n1) TRANSLATE\\r\\n   - \\\"type\\\": \\\"TRANSLATE\\\"\\r\\n   - The \\\"question\\\" is a Vietnamese word or short phrase (VN only).\\r\\n   - The task is to translate it to English.\\r\\n   - \\\"anwser\\\" MUST be exactly one English word (single token; no spaces or hyphens).\\r\\n   - \\\"options\\\" MUST be an empty array [].\\r\\n\\r\\n2) SELECT\\r\\n   - \\\"type\\\": \\\"SELECT\\\"\\r\\n   - The \\\"question\\\" is in English.\\r\\n   - Provide \\\"options\\\" with 3\\u20135 choices; exactly ONE must have \\\"isTrue\\\": true.\\r\\n   - Set \\\"anwser\\\" to an empty string \\\"\\\".\\r\\n   - Keep content aligned with the given topics.\\r\\n\\r\\n3) COMPLETE\\r\\n   - \\\"type\\\": \\\"COMPLETE\\\"\\r\\n   - The \\\"question\\\" is in English and contains a blank (e.g., \\u201CThe cat is ___ the table.\\u201D).\\r\\n   - This is MULTIPLE-CHOICE selection (NOT fill-in): use the \\\"options\\\" list with exactly ONE true choice.\\r\\n   - Set \\\"anwser\\\" to an empty string \\\"\\\".\\r\\n\\r\\nGENERAL\\r\\n- By default, generate across ALL THREE types and distribute the total as evenly as possible. If not divisible, assign the remainder starting from TRANSLATE, then SELECT, then COMPLETE.\\r\\n- All items must relate to the provided topics.\\r\\n- No duplicate questions. Keep language natural and level-appropriate.\\r\\n- Always include the \\\"options\\\" array (empty for TRANSLATE; populated for SELECT and COMPLETE).\\r\\n- Always include the \\\"type\\\" field for each question.\\r\\n- Use the exact key name \\\"anwser\\\" as shown above.\\r\\n\\r\\nINPUT PLACEHOLDERS (replace these with real values before calling the API):\\r\\ntopics = <topics>          \\/\\/ e.g., [\\\"Animals\\\", \\\"Food\\\", \\\"Daily Activities\\\"]\\r\\ntotal  = <total>           \\/\\/ e.g., 12\\r\\n")
    ;

    private String value;
}
