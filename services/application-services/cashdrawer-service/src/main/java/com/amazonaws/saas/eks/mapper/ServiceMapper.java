package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.TransactionResponse;
import com.amazonaws.saas.eks.order.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceMapper {
    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    TransactionResponse transactionToTransactionResponse(Transaction transaction);
}
