-- parcel
alter table parcel MODIFY COLUMN user_id varchar(50) null after parcel_id;
alter table parcel MODIFY COLUMN carrier varchar(50) null after user_id;
alter table parcel MODIFY COLUMN waybill_num varchar(50) null after carrier;
alter table parcel MODIFY COLUMN alias varchar(50) null after waybill_num;
alter table parcel MODIFY COLUMN delivery_status varchar(20) null after alias;
alter table parcel MODIFY COLUMN status varchar(15) null after delivery_status;
alter table parcel MODIFY COLUMN reg_dte datetime(6) null after status;
alter table parcel MODIFY COLUMN audit_dte datetime(6) null after reg_dte;
alter table parcel MODIFY COLUMN arrival_dte datetime(6) null after audit_dte;
alter table parcel MODIFY COLUMN inquiry_hash varchar(100) null after arrival_dte;
alter table parcel MODIFY COLUMN inquiry_result text null after inquiry_hash;
