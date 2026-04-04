package com.v2ray.ang.composeui.bridge.vpn

data class RegionSelectionItem(
    val id: String,
    val name: String,
    val countryCode: String,
    val city: String,
    val latency: Int,
    val load: Int,
    val isPremium: Boolean = false,
)

object RegionSelectionProvider {
    private val regionCatalog = mapOf(
        "us-la" to RegionSelectionItem("us-la", "美国 - 洛杉矶", "US", "洛杉矶", 45, 35),
        "us-ny" to RegionSelectionItem("us-ny", "美国 - 纽约", "US", "纽约", 68, 42),
        "us-sf" to RegionSelectionItem("us-sf", "美国 - 旧金山", "US", "旧金山", 52, 28, true),
        "uk-lon" to RegionSelectionItem("uk-lon", "英国 - 伦敦", "UK", "伦敦", 85, 55),
        "de-ber" to RegionSelectionItem("de-ber", "德国 - 柏林", "DE", "柏林", 78, 48),
        "jp-tok" to RegionSelectionItem("jp-tok", "日本 - 东京", "JP", "东京", 35, 62, true),
        "sg-sin" to RegionSelectionItem("sg-sin", "新加坡", "SG", "新加坡", 25, 70),
        "hk-hk" to RegionSelectionItem("hk-hk", "中国香港", "HK", "香港", 15, 85),
        "kr-seo" to RegionSelectionItem("kr-seo", "韩国 - 首尔", "KR", "首尔", 42, 38),
        "au-syd" to RegionSelectionItem("au-syd", "澳大利亚 - 悉尼", "AU", "悉尼", 95, 25),
        "ca-tor" to RegionSelectionItem("ca-tor", "加拿大 - 多伦多", "CA", "多伦多", 72, 33),
        "fr-par" to RegionSelectionItem("fr-par", "法国 - 巴黎", "FR", "巴黎", 80, 45),
    )

    private val defaultOrder = listOf(
        "us-la", "us-ny", "us-sf", "uk-lon", "de-ber", "jp-tok",
        "sg-sin", "hk-hk", "kr-seo", "au-syd", "ca-tor", "fr-par",
    )

    fun getRegions(allowedRegionIds: List<String> = emptyList()): List<RegionSelectionItem> {
        val ids = if (allowedRegionIds.isEmpty()) defaultOrder else allowedRegionIds
        return ids.mapNotNull { regionCatalog[it] }.ifEmpty {
            defaultOrder.mapNotNull { regionCatalog[it] }
        }
    }
}
