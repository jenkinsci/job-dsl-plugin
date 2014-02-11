package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.promotions.PromotionsContext;
import javaposse.jobdsl.dsl.helpers.promotions.PromotionsContextHelper;
import spock.lang.Specification

public class PromotionsHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    Map<String, List<WithXmlAction>> mockActionsPromotions = Mock()
    PromotionsContextHelper helper = new PromotionsContextHelper(mockActions, mockActionsPromotions, JobType.Freeform)
    PromotionsContext context = new PromotionsContext()

    def 'base promotion usage'() {
        when:
        context.promotion("myPromotionName")

        then:
        context.promotionNodes != null
        context.promotionNodes.size() == 1
		context.promotionNodes["myPromotionName"].name() == 'string'
		context.promotionNodes["myPromotionName"].text() == 'myPromotionName'

        context.subPromotionNodes != null
        context.subPromotionNodes.values().size() == 1
        context.subPromotionNodes["myPromotionName"].name() == 'project'
    }

    def 'promotion name argument cant be null'() {
        when:
        context.promotion(null)

        then:
        thrown(NullPointerException)
    }

    def 'promotion name argument cant be empty'() {
        when:
        context.promotion('',null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'multiple promotions are just fine'() {
        when:
        context.promotion('myFirstPromotion',null)
        context.promotion('mySecondPromotion',null)

        then:
        context.promotionNodes != null
        context.promotionNodes.size() == 2
        context.promotionNodes["myFirstPromotion"].name() == 'string'
        context.promotionNodes["myFirstPromotion"].text() == 'myFirstPromotion'
    }

    def 'promotion already defined'() {
        when:
        context.promotion('one',null)
        context.promotion('one',null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'call promotions via helper'() {
        when:
        helper.promotions {
            promotion('myPromotionName',null)
        }

        then:
        1 * mockActions.add(_)
    }
}
