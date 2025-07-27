package com.example.WordWise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PromptEnum {
    GEN_NEW_FEED("Based on the following news summaries, generate two English vocabulary words for each topic, along with:\\n\\nThe meaning or usage of each word,\\n\\nA sample sentence for each word,\\nand provide Vietnamese translations for both meaning and sentence.\\n\\n\uD83D\uDD14 Important: The output must strictly follow this format:\\ntopic1-word1-eng: <English word>\\ntopic1-word1-vn: <Vietnamese meaning>\\ntopic1-usage1-eng: <English usage/definition>\\ntopic1-usage1-vn: <Vietnamese usage/definition>\\ntopic1-sentence1-eng: <Sample sentence in English>\\ntopic1-sentence1-vn: <Translated sentence in Vietnamese>\\nThen continue similarly for:\\n\\ntopic1-word2\\n\\ntopic2-word1, topic2-word2, etc.");
    private String value;
}
