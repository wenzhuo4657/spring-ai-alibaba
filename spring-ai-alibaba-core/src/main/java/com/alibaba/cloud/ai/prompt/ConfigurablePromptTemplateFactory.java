package com.alibaba.cloud.ai.prompt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.cloud.nacos.annotation.NacosConfigListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class ConfigurablePromptTemplateFactory {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurablePromptTemplateFactory.class);

	private final Map<String, ConfigurablePromptTemplate> templates = new ConcurrentHashMap<>();

	public ConfigurablePromptTemplate create(String name, Resource resource) {
		return templates.computeIfAbsent(name, k -> new ConfigurablePromptTemplate(name, resource));
	}

	public ConfigurablePromptTemplate create(String name, String template) {
		return templates.computeIfAbsent(name, k -> new ConfigurablePromptTemplate(name, template));
	}

	public ConfigurablePromptTemplate create(String name, String template, Map<String, Object> model) {
		return templates.computeIfAbsent(name, k -> new ConfigurablePromptTemplate(name, template, model));
	}

	public ConfigurablePromptTemplate create(String name, Resource resource, Map<String, Object> model) {
		return templates.computeIfAbsent(name, k -> new ConfigurablePromptTemplate(name, resource, model));
	}

	@NacosConfigListener(dataId = "spring.ai.alibaba.configurable.prompt", group = "DEFAULT_GROUP", initNotify = true)
	protected void onConfigChange(List<ConfigurablePromptTemplateModel> configList) {
		if (CollectionUtils.isEmpty(configList)) {
			return;
		}
		for (ConfigurablePromptTemplateModel configuration : configList) {
			if (!StringUtils.hasText(configuration.name()) || !StringUtils.hasText(configuration.template())) {
				continue;
			}
			templates.put(configuration.name(), new ConfigurablePromptTemplate(configuration.name(),
					configuration.template(), configuration.model() == null ? new HashMap<>() : configuration.model()));

			logger.info("OnPromptTemplateConfigChange,templateName:{},template:{},model:{}", configuration.name(),
					configuration.template(), configuration.model());
		}
	}

	public ConfigurablePromptTemplate getTemplate(String name) {
		return templates.get(name);
	}

	public record ConfigurablePromptTemplateModel(String name, String template, Map<String, Object> model) {

	}

}