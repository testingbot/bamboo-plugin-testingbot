[#if plan.buildDefinition.customConfiguration.get('custom.testingbot.enabled')?has_content ]
	
	[@ui.bambooInfoDisplay title='TestingBot' float=false height='80px']
    	[/@ui.bambooInfoDisplay]
[/#if]