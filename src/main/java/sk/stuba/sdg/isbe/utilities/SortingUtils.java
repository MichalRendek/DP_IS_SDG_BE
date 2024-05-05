package sk.stuba.sdg.isbe.utilities;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sk.stuba.sdg.isbe.domain.enums.SortDirectionEnum;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public final class SortingUtils {

    private static final String NO_SORT = "NONE";
    private static final String NO_SORT_NULL = "NULL";

    public static SortDirectionEnum getSortDirection(String sortDirection) {
        try {
            return Enum.valueOf(SortDirectionEnum.class, sortDirection.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundCustomException("Sort direction: '" + sortDirection + "' does not exist! Possible options are ASC, DESC or NONE!");
        }
    }

    public static Sort getDirectedSorting(Sort sorting, String sortDirection) {
        SortDirectionEnum sortDirectionEnum = getSortDirection(sortDirection);
        if (SortDirectionEnum.NONE == sortDirectionEnum) {
            return sorting;
        }
        return SortDirectionEnum.ASC == sortDirectionEnum ? sorting.ascending() : sorting.descending();
    }

    public static String getValidSortingField(String sortBy, Class<?> klass) {
        List<String> sortingFields = Arrays.stream(klass.getDeclaredFields())
                .map(Field::getName)
                .toList();
        for (String field : sortingFields) {
            if (field.equalsIgnoreCase(sortBy)) {
                return field;
            }
        }
        throw new NotFoundCustomException("Sorting field: '" + sortBy + "' can't be found in " + klass.getSimpleName() + " class!" +
                " Possible sorting fields: " + String.join(", ", sortingFields));
    }

    public static Pageable getPagination(Class<?> klass, String sortBy, String sortDirection, int page, int pageSize) {
        if (page <= 0) {
            throw new InvalidOperationException("Page must be greater than 0!");
        }
        if (pageSize <= 0) {
            throw new InvalidOperationException("Size of the page must be greater than 0!");
        }

        Sort sorting = getSort(klass, sortBy, sortDirection);
        return PageRequest.of(page - 1, pageSize, sorting);
    }

    public static Sort getSort(Class<?> klass, String sortBy, String sortDirection) {
        if (sortBy == null || NO_SORT.equalsIgnoreCase(sortBy) || NO_SORT_NULL.equalsIgnoreCase(sortBy) ||
                NO_SORT_NULL.equalsIgnoreCase(sortDirection) || sortDirection == null || SortDirectionEnum.NONE == getSortDirection(sortDirection)) {
            return Sort.unsorted();
        }
        return getDirectedSorting(Sort.by(getValidSortingField(sortBy, klass)), sortDirection);
    }

    public static Pageable getFirstEntry(Class<?> klass) {
        return getPagination(klass, NO_SORT, NO_SORT, 1, 1);
    }
}
