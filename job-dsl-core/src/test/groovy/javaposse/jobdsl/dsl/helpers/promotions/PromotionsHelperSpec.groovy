package javaposse.jobdsl.dsl.helpers.promotions

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import spock.lang.Specification

class PromotionsHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    List<Item> mockAdditionalConfigs = []
    PromotionsContextHelper helper = new PromotionsContextHelper(mockActions, mockAdditionalConfigs, JobType.Freeform)
    PromotionsContext context = new PromotionsContext()

    def 'base promotion usage'() {
        when:
        context.promotion('myPromotionName')

        then:
        context.promotionNodes != null
        context.promotionNodes.size() == 1
        context.promotionNodes['myPromotionName'].name() == 'string'
        context.promotionNodes['myPromotionName'].text() == 'myPromotionName'

        context.subPromotionNodes != null
        context.subPromotionNodes.values().size() == 1
        context.subPromotionNodes['myPromotionName'].name() == 'project'
    }

    def 'promotion name argument cant be null'() {
        when:
        context.promotion(null)

        then:
        thrown(NullPointerException)
    }

    def 'promotion name argument cant be empty'() {
        when:
        context.promotion('', null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'multiple promotions are just fine'() {
        when:
        context.promotion('myFirstPromotion', null)
        context.promotion('mySecondPromotion', null)

        then:
        context.promotionNodes != null
        context.promotionNodes.size() == 2
        context.promotionNodes['myFirstPromotion'].name() == 'string'
        context.promotionNodes['myFirstPromotion'].text() == 'myFirstPromotion'
    }

    def 'promotion already defined'() {
        when:
        context.promotion('one', null)
        context.promotion('one', null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'big promotions list'() {
        when:
        context.promotion('dev') {
            icon('star')
            conditions {
                manual('name')
            }
            actions {
                shell('echo hallo;')
            }
        }
        context.promotion('test') {
            icon('ball')
        }

        then:
        context.promotionNodes != null
        context.promotionNodes.size() == 2
        context.promotionNodes['test'].value() == 'test'
        context.promotionNodes['dev'].value() == 'dev'

        context.subPromotionNodes['test'].'icon'[0].value() == 'ball'
        context.subPromotionNodes['dev'].'icon'[0].value() == 'star'

        context.subPromotionNodes['dev'].'buildSteps'[0].value()[0].name() == 'hudson.tasks.Shell'
        context.subPromotionNodes['dev'].'buildSteps'[0].value()[0].value()[0].value() == 'echo hallo;'
    }

    def 'call promotions via helper'() {
        when:
        helper.promotions { promotion('myPromotionName', null) }

        then:
        1 * mockActions.add(_)
    }
}
