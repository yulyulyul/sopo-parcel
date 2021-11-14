package team.sopo.parcel.domain

import team.sopo.parcel.ParcelInfo
import team.sopo.parcel.domain.update.UpdateResult

interface ParcelService {
    fun retrieveParcel(command: ParcelCommand.GetParcel): ParcelInfo.Main
    fun retrieveOngoingParcels(command: ParcelCommand.GetOngoingParcels): List<ParcelInfo.Main>
    fun retrieveCompleteParcels(command: ParcelCommand.GetCompleteParcels): List<ParcelInfo.Main>
    fun retrieveMonthlyParcelCntList(command: ParcelCommand.GetMonthlyParcelCnt): List<ParcelInfo.MonthlyParcelCnt>
    fun retrieveUsageInfo(getUsageInfoCommand: ParcelCommand.GetUsageInfo): ParcelInfo.UsageInfo
    fun changeParcelAlias(changeAliasCommand: ParcelCommand.ChangeParcelAlias)
    fun deleteParcel(deleteCommand: ParcelCommand.DeleteParcel)
    fun registerParcel(registerCommand: ParcelCommand.RegisterParcel): ParcelInfo.Main
    fun singleRefresh(refreshCommand: ParcelCommand.SingleRefresh): ParcelInfo.RefreshedParcel
    fun entireRefresh(refreshCommand: ParcelCommand.EntireRefresh): List<ParcelInfo.Main>
}