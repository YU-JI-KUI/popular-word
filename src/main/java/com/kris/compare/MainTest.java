package com.kris.compare;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response 中一个 DealVo 和 List<Facility>
 *     Y Deal 中普通属性的值有变化。
 *     Y  Deal 的 change detail 中多一条记录。
 *     Y Deal 中 List<String> 有变化。
 *     Y  只会在 change detail 中添加一条记录。
 *     Y Deal 中 List<Fee> 有变化.
 *     Y  需要验证 List<Fee> 中每一个 Fee 的变化，并将每一个 Fee 的变化设置到 Fee 的属性 change detail 中。
 *     Y 如果有新增加/删除的 Fee，添加一条 change type 为 Node 的 change detail, 如果 fee 被 remove 了，需要为 current Deal 中的 fee list 添加一条空的 fee 占位符（空 Fee 对象），
 */
public class MainTest {

    public static void main(String[] args) {
        var last = getResponse();
        var current = getResponse();

        current.getDeal().setDealName("Update");
        current.getDeal().setCodes(null);

        current.getDeal().getFees().get(0).setFeeType("update");
        current.getDeal().getFees().get(0).setFeeAmount(new BigDecimal(10));

        current.getDeal().getFees().get(1).setFeeId("update");

        current.compareAndRecordChanges(last, current);

        print(last, current);
    }

    private static Response getResponse() {
        var response = new Response();
        response.setDeal(getDeal());
        return response;
    }

    private static Deal getDeal() {
        var deal = new Deal();
        deal.setDealId("deal id");
        deal.setDealName("deal name");
        deal.setCodes(List.of("A", "B", "C"));

        var fee1 = new Fee();
        fee1.setFeeId("fee1");
        fee1.setFeeType("type1");
        fee1.setFeeAmount(new BigDecimal(1));

        var fee2 = new Fee();
        fee2.setFeeId("fee2");
        fee2.setFeeType("type2");
        fee2.setFeeAmount(new BigDecimal(2));

        deal.setFees(List.of(fee1, fee2));
        return deal;
    }

    private static void print(Response last, Response current) {
        System.out.println("Deal Last Changes:");
        last.getDeal().getChangeDetails().forEach(System.out::println);

        System.out.println();

        System.out.println("Deal Current Changes:");
        current.getDeal().getChangeDetails().forEach(System.out::println);

        System.out.println();

        System.out.println("Deal-Fee Last Changes:");
        last.getDeal().getFees().stream().flatMap(e -> e.getChangeDetails().stream()).forEach(System.out::println);

        System.out.println();

        System.out.println("Deal-Fee Current Changes:");
        current.getDeal().getFees().stream().flatMap(e -> e.getChangeDetails().stream()).forEach(System.out::println);
    }

}
