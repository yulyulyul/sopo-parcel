package team.sopo.common.util

import team.sopo.parcel.domain.vo.ParcelCntInfo
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object MonthListBuilder {

    fun makeMonthlyDataList(incompleteMonthList: List<ParcelCntInfo>): MutableList<ParcelCntInfo> {
        val monthList = incompleteMonthList.toMutableList()
        // 정렬해서 상위 12개 아이템만 리턴할 객체
        val returnObj = mutableListOf<ParcelCntInfo>()

        /*
        * 현재날짜(ex, 2020-01)이 기준으로 가입된 날짜까지 정렬해서 가져오는 것이므로
        * 현재날짜의 데이터를 정의한다.
        */
        var currentYearMonth = YearMonth.now()
        // 디비로부터 받아온 데이터의 포멧이 yyyy-MM이므로 해당 데이터를 YearMonth로 쉽게 바꾸기 위하여 DateTimeFormatter를 정의한다.

        /*
         * 디비로부터 받아온 리스트(monthListByUserName)에서 가장 첫 번째 데이터(timeCountDto)를 추출해서
         * 현재 기준 날짜 데이터(currentYearMonth)가 같으면 monthListByUserName에서 제외시킨다.
         *
         * (현재 기준 날짜 데이터를 기으로 유저가 회원가입한 날짜의 '월'까지 내림차순으로 필요하기 때문에
         *   현재 기준 날자 데이터(currentYearMonth) == 가장 첫 번째 데이터(timeCountDto)이면 => 유효한 데이터
         *   현재 기준 날자 데이터(currentYearMonth) != 가장 첫 번째 데이터(timeCountDto)이면 => 유효하지 않음 => 따라서 현재 기준 날짜 데이터(currentYearMonth)에 Count를 0으로 채워서 리스트를 채워야함)
         *
         * if
         *  디비로부터 받아온 리스트 => [(2020-10, 1),  (2020-08, 2), (2020-07, 3)]
         *  현재 기준 날짜 데이터 => 2020-11
         *  결과 데이터 => [(2020-11, 0) (2020-10, 1), (2020-08, 2), (2020-07, 3)]
         */

        // 데이터를 12개월만 가지고 있을 것이므로 12개 이상의 데이터는 필요 없음.
        var cnt = 1
        while (monthList.isNotEmpty()) { // 12개까지 안가더라도 데이터가 비었으면 그만한다.
            if (cnt > 12) break

            // 디비로부터 받아온 리스트의 가장 첫 번째
            val targetTimeCountDTO = monthList.first()
            val targetTimeCount = getYearMonth(targetTimeCountDTO)

            if (currentYearMonth.year == targetTimeCount.year && currentYearMonth.monthValue == targetTimeCount.monthValue) {
                returnObj.add(targetTimeCountDTO)
                monthList.removeAt(0)
            } else { // 사용자가 해당 월에 사용하지 않았을 경우, count를 0을 채운 TimeCountDTO를 만들어 returnObj에 넣어준다.
                returnObj.add(ParcelCntInfo("${currentYearMonth.year}-${String.format("%02d", currentYearMonth.monthValue)}", 0))
            }
            currentYearMonth = currentYearMonth.minusMonths(1)
            cnt++
        }
        return returnObj
    }

    private fun getYearMonth(parcelCntInfo: ParcelCntInfo): YearMonth {
        return YearMonth.parse(parcelCntInfo.time, DateTimeFormatter.ofPattern("yyyy-MM"))
    }
}