package team.sopo.parcel.application

import org.springframework.stereotype.Service
import team.sopo.parcel.ParcelInfo
import team.sopo.parcel.domain.ParcelCommand
import team.sopo.parcel.domain.ParcelService
import team.sopo.push.domain.PushService

@Service
class ParcelFacade(
    private val parcelService: ParcelService,
    private val pushService: PushService
) {

    fun retrieveParcel(command: ParcelCommand.GetParcel): ParcelInfo.Main{
        return parcelService.retrieveParcel(command)
    }

    fun retrieveOngoingParcels(command: ParcelCommand.GetOngoingParcels): List<ParcelInfo.Main>{
        return parcelService.retrieveOngoingParcels(command)
    }

    fun retrieveCompleteParcels(command: ParcelCommand.GetCompleteParcels): List<ParcelInfo.Main>{
        return parcelService.retrieveCompleteParcels(command)
    }

    fun retrieveMonthlyParcelCnt(command: ParcelCommand.GetMonthlyParcelCnt): List<ParcelInfo.MonthlyParcelCnt> {
        return parcelService.retrieveMonthlyParcelCntList(command)
    }

    fun retrieveUsageInfo(command: ParcelCommand.GetUsageInfo): ParcelInfo.UsageInfo{
        return parcelService.retrieveUsageInfo(command)
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
        val updateCompleteParcels = parcelService.entireRefresh(command)
        pushService.pushCompleteParcels(command.userId, updateCompleteParcels)
    }

    fun pushParcels(command: ParcelCommand.PushRequest){
        pushService.pushCompleteParcels(command.userId, command.parcelIds)
    }
}