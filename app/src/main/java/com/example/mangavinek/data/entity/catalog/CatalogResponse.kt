package com.example.mangavinek.data.entity.catalog

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class CatalogResponse(element: Element? = null){
    val title = element?.select(".thead")?.text().toString()
    val image = element?.select("img")?.attr("src").toString()
    val url = element?.select("a")?.attr("href").toString()

    fun addElements(elements: Elements): List<CatalogResponse> {
        val listElements = mutableListOf<CatalogResponse>()
        val listElementsFilter = mutableListOf<CatalogResponse>()

        elements.mapNotNull {
            listElements.add(CatalogResponse(it))
        }

        listElements.forEach {
            if (it.title != "Detalhes") {
                listElementsFilter.add(it)
            }
        }

        return listElementsFilter
    }
}