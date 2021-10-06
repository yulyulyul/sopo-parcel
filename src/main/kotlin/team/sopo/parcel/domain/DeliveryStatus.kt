package team.sopo.parcel.domain

enum class DeliveryStatus {
    NOT_REGISTERED,
    CHANGED,
    UNCHANGED,
    ORPHANED,
    DELIVERED, // 배송완료
    OUT_FOR_DELIVERY, // 배송출발
    IN_TRANSIT, // 상품이동중
    AT_PICKUP, // 상품인수
    INFORMATION_RECEIVED // 상품준비중
}