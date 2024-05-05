package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Dto를 따로만든 이유 리파지토리가 컨트롤러를 참조하는 의존관계가 순환이 되어버림
//또한 엔티티 조회방식은  범용성이 높아 재사용이 가능한 반면 Dto 직접조회는 리파지토리가 하나의 dto에 제한적이므로 따로 만듬
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> orders = findOrders(); //셀렉트 조회 쿼리 나감
        orders.forEach(o ->{//조회 쿼리의 결과 orders 즉 N개만큼 아이템 셀렉트 쿼리나감
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems); //조회한 아이템을 설정
        });
        return orders;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> orders = findOrders(); //셀렉트 조회 쿼리 나감
        List<Long> orderIds = toOrderIds(orders);

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderMap(orderIds);

        orders.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId()))); //반복문으로 설정

        return orders;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class) //in 쿼리로 주문번호에 맞는 아이템들을 다가져옴
                .setParameter("orderIds", orderIds)
                .getResultList();

        //groupingBy로 리스트를 Map으로 바꿔줌 key=orderItemQueryDto.getOrderId() value=orderItemQueryDto
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderItemMap;
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> orders) {
        List<Long> orderIds = orders.stream() //조회된 주문 id를 가져옴
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, d.address, o.orderDate,o.status)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"+
                        " from OrderItem oi"+
                        " join oi.item i"+
                        " where oi.order.id = :orderId",OrderItemQueryDto.class)
                .setParameter("orderId",orderId)
                .getResultList();
    }





}
