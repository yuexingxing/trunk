package com.zhiduan.crowdclient.menuindex.filterlistview;

import java.io.Serializable;
import java.util.List;


/**
 * hexiuhui
 */
public class FilterData implements Serializable {

    private List<FilterEntity> category;
    private List<FilterEntity> sorts;
    private List<FilterEntity> filters;

    public List<FilterEntity> getCategory() {
        return category;
    }

    public void setCategory(List<FilterEntity> category) {
        this.category = category;
    }

    public List<FilterEntity> getSorts() {
        return sorts;
    }

    public void setSorts(List<FilterEntity> sorts) {
        this.sorts = sorts;
    }

    public List<FilterEntity> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterEntity> filters) {
        this.filters = filters;
    }
}
