package se.sundsvall.permitloader.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "procapita_raw")
public class ProcapitaRawEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "personal_number", updatable = false)
	private String personalNumber;

	@Column(name = "assistance_type", updatable = false)
	private String assistanceType;

	@Column(name = "duration", updatable = false)
	private String duration;

	@Column(name = "start_date", updatable = false)
	private LocalDate startDate;

	@Column(name = "end_date", updatable = false)
	private LocalDate endDate;

	@Column(name = "permit_group", updatable = false)
	private String permitGroup;

	@Column(name = "party_id")
	private String partyId;

	@Column(name = "party_asset_id")
	private String partyAssetId;

	@Column(name = "status", columnDefinition = "longtext")
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getPersonalNumber() {
		return personalNumber;
	}

	public void setPersonalNumber(final String personalNumber) {
		this.personalNumber = personalNumber;
	}

	public String getAssistanceType() {
		return assistanceType;
	}

	public void setAssistanceType(final String assistanceType) {
		this.assistanceType = assistanceType;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(final String duration) {
		this.duration = duration;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(final LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(final LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getPermitGroup() {
		return permitGroup;
	}

	public void setPermitGroup(final String permitGroup) {
		this.permitGroup = permitGroup;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(final String partyId) {
		this.partyId = partyId;
	}

	public String getPartyAssetId() {
		return partyAssetId;
	}

	public void setPartyAssetId(final String partyAssetId) {
		this.partyAssetId = partyAssetId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ProcapitaRawEntity that = (ProcapitaRawEntity) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "ProcapitaRawEntity{" +
			"id=" + id +
			", personalNumber='" + personalNumber + '\'' +
			", assistanceType='" + assistanceType + '\'' +
			", duration='" + duration + '\'' +
			", startDate=" + startDate +
			", endDate=" + endDate +
			", permitGroup='" + permitGroup + '\'' +
			", partyId='" + partyId + '\'' +
			", partyAssetId='" + partyAssetId + '\'' +
			", status='" + status + '\'' +
			'}';
	}
}
