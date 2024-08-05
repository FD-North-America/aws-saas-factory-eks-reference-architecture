package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.DiscountResponse;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.PaidOutCodeResponse;
import com.amazonaws.saas.eks.dto.responses.stockstatus.StockStatusItem;
import com.amazonaws.saas.eks.order.model.Discount;
import com.amazonaws.saas.eks.order.model.PaidOutCode;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.util.Utils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

@Mapper
public interface ReportMapper {
    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    StockStatusItem productToStockStatusItem(Product p);


    PaidOutCodeResponse paidOutCodeToPaidOutCodeResponse(PaidOutCode paidOutCode, @Context TimeZone timeZone);

    @Mapping(source = "price", target = "amount")
    DiscountResponse discountToDiscountResponse(Discount discount, @Context TimeZone timeZone);

    default Date fromDate(Date date, @Context TimeZone timeZone) throws ParseException {
        return Utils.changeDateTimeZone(date, timeZone);
    }
}
