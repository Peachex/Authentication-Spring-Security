package com.epam.esm.dao.creator.criteria.search;

import com.epam.esm.constant.entity.GiftCertificateFieldName;
import com.epam.esm.dto.GiftCertificate;
import com.epam.esm.dto.Tag;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FullMatchSearchCertificateCriteria extends SearchCertificateCriteria {
    public FullMatchSearchCertificateCriteria(String columnName, String value) {
        super(columnName, value);
    }

    public FullMatchSearchCertificateCriteria(List<Tag> tags) {
        super(tags);
    }

    @Override
    public void acceptCriteria(CriteriaQuery<GiftCertificate> criteriaQuery, CriteriaBuilder builder,
                               Root<GiftCertificate> root) {
        List<Tag> searchedTags = getTags();
        if (CollectionUtils.isNotEmpty(searchedTags)) {
            Expression<Collection<Tag>> tags = root.get(GiftCertificateFieldName.TAGS);
            List<Predicate> containsTags = new ArrayList<>();
            searchedTags.forEach(t -> containsTags.add(builder.isMember(t, tags)));
            Predicate finalPredicate = builder.and(containsTags.toArray(new Predicate[0]));
            criteriaQuery.where(finalPredicate);
        } else {
            criteriaQuery.where(builder.equal(root.get(getFieldName()), getValue()));
        }
    }
}
