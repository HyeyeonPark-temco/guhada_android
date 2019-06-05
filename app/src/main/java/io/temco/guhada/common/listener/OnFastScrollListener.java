package io.temco.guhada.common.listener;

import java.util.Map;

public interface OnFastScrollListener {

    // Point
    boolean isAddPoint();

    // Index
    Map<String, Integer> getIndex();

    // Section
    String[] getSections();

    void setCurrentSection(String section);

    String getCurrentSection();

    void setShowSection(boolean isShow);

    boolean getShowSection();

    // Size
    int getHeaderHeight();

    float getTopPadding();

    float getLetterTextSize();

    float getPointTextSize();

    float getSectionTextSize();

    float getSectionViewSize();

    float getSectionViewPadding();

    // Color
    int getColorLetterTextNormal();

    int getColorLetterTextSelect();

    int getColorSectionText();

    int getColorSectionBackground();
}