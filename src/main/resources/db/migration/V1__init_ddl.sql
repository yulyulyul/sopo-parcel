-- parcel
create table parcel
(
    parcel_id       bigint auto_increment primary key,
    status          int          null,
    waybill_num     varchar(255) null,
    user_id         varchar(50)  null,
    reg_dte         datetime(6)  null,
    alias           varchar(255) null,
    arrival_dte     datetime(6)  null,
    audit_dte       datetime(6)  null,
    carrier         varchar(255) null,
    delivery_status varchar(255) null,
    inquiry_hash    varchar(255) null,
    inquiry_result  text         null
) comment 'parcel' charset = utf8mb4;

create
    index parcel_idx01 on parcel (reg_dte);

create
    index parcel_idx02 on parcel (audit_dte);