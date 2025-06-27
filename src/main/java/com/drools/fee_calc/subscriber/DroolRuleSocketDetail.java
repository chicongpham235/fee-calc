package com.drools.fee_calc.subscriber;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.json.JSONObject;

import lombok.Getter;

@Getter
public class DroolRuleSocketDetail {
    private UUID id;
    private String code;
    private String status;

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("code", code);
        jsonObject.put("status", status);
        return jsonObject;
    }

    public byte[] toBytes() {
        return toJSONObject().toString().getBytes(StandardCharsets.UTF_8);
    }
}
