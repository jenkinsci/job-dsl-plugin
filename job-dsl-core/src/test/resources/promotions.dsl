job {
    name 'test'
    promotions {
        promotion('dev') {
            icon('gold-star')
        restrict('dev2')
            conditions {
                selfPromotion(true)
                manual('denschu') {
                    parameters {
                        textParam('name', 'default', 'desc')
                    }
                }
                releaseBuild()
                downstream(true, 'after')
            }
            actions {
                shell('echo hallo')
                downstreamParameterized {
                    trigger('deploy') {
                        currentBuild()
                    }
                }
            }
        }
    }    
}

