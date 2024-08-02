package com.amazonaws.saas.eks.settings.model.v2.pos;

import com.amazonaws.saas.eks.settings.model.v2.pos.converter.DisallowCashReceiptOptionsConverter;
import com.amazonaws.saas.eks.settings.model.v2.pos.converter.PickingTicketPrintOptionsConverter;
import com.amazonaws.saas.eks.settings.model.v2.pos.converter.SequenceNumbersConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@DynamoDBTable(tableName = POSSettings.TABLE_NAME)
public class POSSettings {
    public static final String TABLE_NAME = "Settings_v2";
    public static final String KEY_DELIMITER = "#";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";

        public static final String SORT_KEY = "SortKey";
        public static final String SEQUENCE_NUMBERS = "SequenceNumbers";
        public static final String DISALLOW_CASH_RECEIPT_OPTIONS = "DisallowCashReceiptOptions";
        public static final String PICKING_TICKET_PRINT_OPTIONS = "PickingTicketPrintOptions";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    @Setter
    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @Setter
    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.SEQUENCE_NUMBERS)
    @DynamoDBTypeConverted(converter = SequenceNumbersConverter.class)
    private List<SequenceNumber> sequenceNumbers;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.DISALLOW_CASH_RECEIPT_OPTIONS)
    @DynamoDBTypeConverted(converter = DisallowCashReceiptOptionsConverter.class)
    private DisallowCashReceiptOptions disallowCashReceiptOptions;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.PICKING_TICKET_PRINT_OPTIONS)
    @DynamoDBTypeConverted(converter = PickingTicketPrintOptionsConverter.class)
    private PickingTicketPrintOptions pickingTicketPrintOptions;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    public static String buildPartitionKey(String tenantId) {
        return tenantId;
    }
}
