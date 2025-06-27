package com.drools.fee_calc.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.drools.fee_calc.model.Customer;
import com.drools.fee_calc.model.Transaction;

import jakarta.annotation.PostConstruct;

@Service
public class DroolsEngineService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DroolsEngineService.class);

    @Value("${drools.fee-cal.drl-path}")
    private String drlPath;

    private final KieServices kieServices = KieServices.Factory.get();

    private volatile KieContainer kieContainer;

    @PostConstruct
    public void init() {
        this.reloadRules();
        logger.info("DroolsEngineService initialized and rules loaded from: {}", drlPath);
    }

    public synchronized void reloadRules() {
        logger.info("🔁 Reloading Drools rules from: {}", drlPath);
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        try {
            String originalDrl = Files.readString(Path.of(drlPath), StandardCharsets.UTF_8);

            String pkg = "package rules.fee_" + UUID.randomUUID().toString().replace("-", "_") + ";\n";

            String imports = String.join("\n", List.of(
                    "import " + Transaction.class.getName() + ";",
                    "import " + Customer.class.getName() + ";"));

            String modifiedDrl;
            if (originalDrl.trim().startsWith("package")) {
                int idx = originalDrl.indexOf("\n"); // sau dòng package
                modifiedDrl = originalDrl.substring(0, idx + 1) + imports + "\n" + originalDrl.substring(idx + 1);
            } else {
                modifiedDrl = pkg + "\n" + imports + "\n" + originalDrl;
            }

            logger.info("📄 DRL content:\n{}", modifiedDrl);

            kieFileSystem.write("src/main/resources/rules/generated.drl",
                    ResourceFactory.newByteArrayResource(modifiedDrl.getBytes(StandardCharsets.UTF_8)));

            // Resource resource = ResourceFactory.newFileResource(drlPath);
            // kieFileSystem.write(resource);

            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();

            if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
                kieBuilder.getResults().getMessages(Message.Level.ERROR).forEach(
                        msg -> logger.error("❌ DRL Build Error: {}", msg));
                logger.warn("⚠️ Failed to reload Drools rules due to errors.");
                return;
            }

            KieModule kieModule = kieBuilder.getKieModule();
            this.kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

            logger.info("✅ Reload successful");

        } catch (Exception e) {
            logger.error("❌ Error loading DRL file from path: " + drlPath, e);
        }
    }

    public KieContainer getKieContainer() {
        return kieContainer;
    }
}
