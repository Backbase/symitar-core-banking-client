package com.backbase.accelerators.symitar.client.name.model;

import com.symitar.generated.symxchange.account.dto.retrieve.Name;
import lombok.Data;

import java.util.List;

@Data
public class GetNameRecordsResponse {

    Short accountType;
    List<Name> nameRecords;
}
