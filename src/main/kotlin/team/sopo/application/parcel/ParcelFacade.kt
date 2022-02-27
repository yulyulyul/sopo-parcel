package team.sopo.application.parcel

import org.springframework.stereotype.Service
import team.sopo.domain.parcel.ParcelInfo
import team.sopo.domain.parcel.ParcelCommand
import team.sopo.domain.parcel.ParcelService
import team.sopo.domain.push.PushService

@Service
class ParcelFacade(
    private val pushService: PushService,
    private val parcelService: ParcelService
) {

    fun getParcel(command: ParcelCommand.GetParcel): ParcelInfo.Main{
        return parcelService.getParcel(command)
    }

    fun getParcels(command: ParcelCommand.GetParcels): List<ParcelInfo.Main>{
        return parcelService.getParcels(command)
    }

    fun getOngoingParcels(command: ParcelCommand.GetOngoingParcels): List<ParcelInfo.Main>{
        return parcelService.getOngoingParcels(command)
    }

    fun getCompleteParcels(command: ParcelCommand.GetCompleteParcels): List<ParcelInfo.Main>{
        return parcelService.getCompleteParcels(command)
    }

    fun getMonthlyParcelCnt(command: ParcelCommand.GetMonthlyParcelCnt): List<ParcelInfo.MonthlyParcelCnt> {
        return parcelService.getMonthlyParcelCntList(command)
    }

    fun getUsageInfo(command: ParcelCommand.GetUsageInfo): ParcelInfo.ParcelUsage{
        return parcelService.getUsageInfo(command)
    }

    fun changeParcelAlias(command: ParcelCommand.ChangeParcelAlias){
        parcelService.changeParcelAlias(command)
    }

    fun deleteParcel(command: ParcelCommand.DeleteParcel){
        parcelService.deleteParcel(command)
    }

    fun registerParcel(command: ParcelCommand.RegisterParcel): ParcelInfo.Main{
        return parcelService.registerParcel(command)
    }

    fun singleRefresh(command: ParcelCommand.SingleRefresh): ParcelInfo.RefreshedParcel{
        return parcelService.singleRefresh(command)
    }

    fun entireRefresh(command: ParcelCommand.EntireRefresh){
        parcelService.entireRefresh(command).apply {
            if(this.isNotEmpty()){
                pushService.pushCompleteParcels(command.userId, this)
            }
        }
    }

    fun pushParcels(command: ParcelCommand.PushRequest){
        pushService.pushCompleteParcels(command.userId, command.parcelIds)
    }

    fun pushDeviceAwaken(command: ParcelCommand.DeviceAwakenRequest){
        pushService.pushAwakenDevice(command.topic)
    }

}