dashboardView('example') {
    jobs {
        regex(/acme-.*/)
    }
    columns {
        status()
        weather()
        buildButton()
    }
    topPortlets {
        testStatisticsChart()
    }
    bottomPortlets {
        testStatisticsGrid()
    }
}
