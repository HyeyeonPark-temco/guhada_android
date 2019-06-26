package io.temco.guhada.common.util;

import com.github.florent37.expansionpanel.ExpansionLayout;

import java.util.Collection;
import java.util.HashSet;

public class ExpansionCollection {

    private final Collection<ExpansionLayout> expansions = new HashSet<>();
    private boolean openOnlyOne = true;

    private final ExpansionLayout.IndicatorListener indicatorListener = (expansionLayout, willExpand) -> {
        if (willExpand && openOnlyOne) {
            for (ExpansionLayout view : expansions) {
                if (view != expansionLayout) {
                    view.collapse(true);
                }
            }
        }
    };

    public void collapseAll() {
        for (ExpansionLayout view : expansions) {
            view.collapse(true);
        }
    }

    public ExpansionCollection add(ExpansionLayout expansionLayout) {
        expansions.add(expansionLayout);
        expansionLayout.addIndicatorListener(indicatorListener);
        return this;
    }

    public ExpansionCollection addAll(ExpansionLayout... expansionLayouts) {
        for (ExpansionLayout expansionLayout : expansionLayouts) {
            if (expansionLayout != null) {
                add(expansionLayout);
            }
        }
        return this;
    }

    public ExpansionCollection addAll(Collection<ExpansionLayout> expansionLayouts) {
        for (ExpansionLayout expansionLayout : expansionLayouts) {
            if (expansionLayout != null) {
                add(expansionLayout);
            }
        }
        return this;
    }

    public ExpansionCollection remove(ExpansionLayout expansionLayout) {
        if (expansionLayout != null) {
            expansions.remove(expansionLayout);
            expansionLayout.removeIndicatorListener(indicatorListener);
        }
        return this;
    }

    public ExpansionCollection openOnlyOne(boolean openOnlyOne) {
        this.openOnlyOne = openOnlyOne;
        return this;
    }
}