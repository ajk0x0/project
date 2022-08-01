package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;

/**
 * A UserData.
 */
@Entity
@Table(name = "user_data")
public class UserData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "rf_id")
    private String rfId;

    @Column(name = "restricted")
    private Boolean restricted;

    @Column(name = "count")
    private Integer count;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRfId() {
        return this.rfId;
    }

    public UserData rfId(String rfId) {
        this.setRfId(rfId);
        return this;
    }

    public void setRfId(String rfId) {
        this.rfId = rfId;
    }

    public Boolean getRestricted() {
        return this.restricted;
    }

    public UserData restricted(Boolean restricted) {
        this.setRestricted(restricted);
        return this;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public Integer getCount() {
        return this.count;
    }

    public UserData count(Integer count) {
        this.setCount(count);
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public UserData updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserData)) {
            return false;
        }
        return id != null && id.equals(((UserData) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserData{" +
            "id=" + getId() +
            ", rfId='" + getRfId() + "'" +
            ", restricted='" + getRestricted() + "'" +
            ", count=" + getCount() +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
