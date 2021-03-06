package com.epam.esm.dao.creator;

import com.epam.esm.constant.entity.GiftCertificateFieldName;
import com.epam.esm.dao.creator.criteria.Criteria;
import com.epam.esm.dto.GiftCertificate;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Component
public class GiftCertificateQueryCreator implements QueryCreator<GiftCertificate> {
    @Override
    public void createCriteria(List<Criteria<GiftCertificate>> criteriaList, CriteriaQuery<GiftCertificate>
            criteriaQuery, CriteriaBuilder builder, Root<GiftCertificate> root) {
        criteriaQuery.where(builder.isNotEmpty(root.get(GiftCertificateFieldName.TAGS)), builder.equal(
                root.get(GiftCertificateFieldName.IS_AVAILABLE), true));
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            criteriaList.stream().filter(Objects::nonNull).forEach(c -> c.acceptCriteria(criteriaQuery, builder, root));
        }
    }
}
