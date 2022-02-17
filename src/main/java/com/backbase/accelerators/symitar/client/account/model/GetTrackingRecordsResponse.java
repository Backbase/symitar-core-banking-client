package com.backbase.accelerators.symitar.client.account.model;

import com.symitar.generated.symxchange.account.dto.retrieve.Tracking;
import lombok.Data;

import java.util.List;

@Data
public class GetTrackingRecordsResponse {

    private Short accountType;
    private List<Tracking> trackingRecords;
}
