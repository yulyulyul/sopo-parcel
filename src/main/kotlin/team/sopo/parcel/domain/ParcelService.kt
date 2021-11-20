package team.sopo.parcel.domain

import team.sopo.parcel.ParcelInfo

interface ParcelService {
    fun retrieveParcel(getCommand: ParcelCommand.GetParcel): ParcelInfo.Main
    fun retrieveOngoingParcels(getCommand: ParcelCommand.GetOngoingParcels): List<ParcelInfo.Main>
    fun retrieveCompleteParcels(getCommand: ParcelCommand.GetCompleteParcels): List<ParcelInfo.Main>
    fun retrieveMonthlyParcelCntList(getCommand: ParcelCommand.GetMonthlyParcelCnt): List<ParcelInfo.MonthlyParcelCnt>
    fun retrieveUsageInfo(getCommand: ParcelCommand.GetUsageInfo): ParcelInfo.UsageInfo
    fun changeParcelAlias(changeCommand: ParcelCommand.ChangeParcelAlias)
    fun deleteParcel(deleteCommand: ParcelCommand.DeleteParcel)
    fun registerParcel(registerCommand: ParcelCommand.RegisterParcel): ParcelInfo.Main
    fun singleRefresh(refreshCommand: ParcelCommand.SingleRefresh): ParcelInfo.RefreshedParcel
    fun entireRefresh(refreshCommand: ParcelCommand.EntireRefresh): List<ParcelInfo.Main>
}