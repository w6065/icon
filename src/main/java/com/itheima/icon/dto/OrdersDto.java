package com.itheima.icon.dto;

import com.itheima.icon.entity.OrderDetail;
import com.itheima.icon.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {
    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
}
