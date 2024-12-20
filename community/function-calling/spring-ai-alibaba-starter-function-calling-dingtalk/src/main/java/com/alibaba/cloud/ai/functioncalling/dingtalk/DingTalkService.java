package com.alibaba.cloud.ai.functioncalling.dingtalk;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.util.ObjectUtils;

import java.util.function.Function;

/**
 * @author YunLong
 */
public class DingTalkService implements Function<DingTalkService.Request, DingTalkService.Response> {

	private final DingTalkProperties dingTalkProperties;

	public DingTalkService(DingTalkProperties dingTalkProperties) {
		this.dingTalkProperties = dingTalkProperties;
	}

	/**
	 * The old version of DingTalk SDK. Some interfaces have not been fully replaced yet.
	 * Official Document
	 * Address：https://open.dingtalk.com/document/orgapp/custom-robots-send-group-messages
	 */
	@Override
	public Response apply(Request request) {

		if (ObjectUtils.isEmpty(dingTalkProperties.getCustomRobotAccessToken())
				|| ObjectUtils.isEmpty(dingTalkProperties.getCustomRobotSignature())) {
			throw new IllegalArgumentException("spring.ai.alibaba.plugin.dingtalk.custom-robot must not be null.");
		}

		String accessToken = dingTalkProperties.getCustomRobotAccessToken();
		String signature = dingTalkProperties.getCustomRobotSignature();

		// Request Body, please see the official document for more parameters.
		OapiRobotSendRequest req = new OapiRobotSendRequest();
		req.setMsgtype("text");
		req.setText(String.format("{\"content\":\"%s\"}", request.message()));

		try {
			DingTalkClient client = new DefaultDingTalkClient(
					String.format("https://oapi.dingtalk.com/robot/send?%s", SignUtils.getSign(signature)));
			OapiRobotSendResponse response = client.execute(req, accessToken);

			if (response.isSuccess()) {
				return new Response("The custom robot message was sent successfully!");
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Custom robot message sending failed!");
		}

		return null;
	}

	@JsonClassDescription("Send group chat messages using a custom robot")
	public record Request(@JsonProperty(required = true,
			value = "message") @JsonPropertyDescription("Customize what the robot needs to send") String message) {
	}

	public record Response(String message) {
	}

}
