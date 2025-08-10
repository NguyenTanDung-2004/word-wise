package com.example.WordWise.mapper;

import com.example.WordWise.entity.ExtensionReview;
import com.example.WordWise.entity.Word;
import com.example.WordWise.enums.ExtensionReviewTypeEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("reviewExtensionMapper")
public class ReviewExtensionMapper implements Mapper{
    @Override
    public Object map(Object type, Object word) {
        ExtensionReviewTypeEnum enumType = (ExtensionReviewTypeEnum) type;
        ExtensionReview extensionReview = new ExtensionReview();

        switch (enumType) {
            case SELECT:
                return mapSELECT((Word) word, extensionReview);
            case TRANSLATE:
                return mapTRANSLATE((Word) word, extensionReview);
            case COMPLETE:
                return mapCOMPLETE((Word) word, extensionReview);
            default:
                break;
        }

        return null;
    }

    private ExtensionReview mapTRANSLATE(Word word, ExtensionReview extensionReview) {
        extensionReview.setExtensionReviewType(ExtensionReviewTypeEnum.TRANSLATE);
        extensionReview.setVn(word.getVietnameseWord());
        extensionReview.setEng(word.getEnglishWord());
        extensionReview.setTestDate(LocalDateTime.now());
        extensionReview.setWordId(word.getId());
        extensionReview.setUserId(word.getUserId());

        return extensionReview;
    }

    private ExtensionReview mapCOMPLETE(Word word, ExtensionReview extensionReview) {
        extensionReview.setExtensionReviewType(ExtensionReviewTypeEnum.COMPLETE);
        extensionReview.setVn(word.getVietnameseWord());
        extensionReview.setEng(word.getEnglishWord());
        extensionReview.setTestDate(LocalDateTime.now());
        extensionReview.setWordId(word.getId());
        extensionReview.setUserId(word.getUserId());

        return extensionReview;
    }

    private ExtensionReview mapSELECT(Word word, ExtensionReview extensionReview) {
        extensionReview.setExtensionReviewType(ExtensionReviewTypeEnum.SELECT);
        extensionReview.setVn(word.getVietnameseWord());
        extensionReview.setEng(word.getEnglishWord());
        extensionReview.setTestDate(LocalDateTime.now());
        extensionReview.setWordId(word.getId());
        extensionReview.setUserId(word.getUserId());
        if (word.getDescription() != null) {
            extensionReview.setDescription(word.getDescription());
            extensionReview.setOptions(word.getOptions());
        }
        return extensionReview;
    }
}
