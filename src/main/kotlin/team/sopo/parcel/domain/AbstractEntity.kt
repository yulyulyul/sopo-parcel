package team.sopo.parcel.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class AbstractEntity {
    @CreationTimestamp
    @Column(name = "reg_dte")
    var regDte: ZonedDateTime? = null

    @UpdateTimestamp
    @Column(name = "audit_dte")
    var auditDte: ZonedDateTime? = null
}