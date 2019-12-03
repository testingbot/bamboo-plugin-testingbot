package com.testingbot.bamboo.plan;

import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plan.cache.CachedPlanManager;
import com.atlassian.bamboo.plan.cache.ImmutableChain;
import com.atlassian.bamboo.plan.cache.ImmutableJob;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.testingbot.bamboo.config.MappedBuildConfiguration;

import java.util.Map;

public class ViewTestingBotCondition implements Condition {
    private PlanManager planManager;
    private CachedPlanManager cachedPlanManager;

    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }

    public void setCachedPlanManager(CachedPlanManager cachedPlanManager) {
        this.cachedPlanManager = cachedPlanManager;
    }

    @Override
    public void init(Map<String, String> map) throws PluginParseException {
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        if (!context.containsKey("planKey") || !context.containsKey("buildKey")) { return true; }

        final ImmutableChain chain;
        PlanKey planKey = PlanKeys.getPlanKey(context.get("planKey").toString());
        if (PlanKeys.isChainKey(planKey)) {
            chain = cachedPlanManager.getPlanByKey(planKey, ImmutableChain.class);
        } else {
            chain = cachedPlanManager.getPlanByKeyIfOfType(PlanKeys.getChainKeyFromJobKey(planKey), ImmutableChain.class);
        }

        if (chain == null) { return false; }

        for (ImmutableJob job: chain.getAllJobs()) {
            MappedBuildConfiguration sodMappedBuildConfiguration = new MappedBuildConfiguration(job.getBuildDefinition().getCustomConfiguration());
            if (sodMappedBuildConfiguration.isEnabled()) {
                return true;
            }
        }
        return false;
    }
}
