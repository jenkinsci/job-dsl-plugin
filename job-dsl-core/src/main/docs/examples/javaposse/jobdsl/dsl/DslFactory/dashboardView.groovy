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
        jenkinsJobsList {
            displayName('acme jobs')
        }
        image {
            displayName('example image')
            url('https://example.com/example.png')
        }
    }
    leftPortlets {
        testStatisticsChart()
    }
    rightPortlets {
        testTrendChart()
    }
    bottomPortlets {
        iframe {
            effectiveUrl('http://example.com')
        }
        testStatisticsGrid()
        buildStatistics()
    }
}
