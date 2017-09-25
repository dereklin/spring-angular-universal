package com.example.demo;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class DemoApplication {

// 	public static void main(String[] args) {
// 		SpringApplication.run(DemoApplication.class, args);
// 	}
// }

import ch.swaechter.angularjuniversal.renderer.Renderer;
import ch.swaechter.angularjuniversal.renderer.assets.RenderAssetProvider;
import ch.swaechter.angularjuniversal.renderer.assets.ResourceProvider;
import ch.swaechter.angularjuniversal.v8renderer.V8RenderEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Controller
    public class DemoController {

        private final DemoService demoservice;

        @Autowired
        public DemoController(DemoService demoservice) {
            this.demoservice = demoservice;
        }

        @ResponseBody
        @GetMapping("/test")
        public String showIndex() throws Exception {
            return demoservice.renderPage("/").get();
        }
    }

    @Service
    public class DemoService {

        private final Renderer renderer;

        public DemoService() throws IOException {
            // Get our index.html template and the relocatable server bundle
            InputStream indexinputstream = getClass().getResourceAsStream("/public/index.html");
            InputStream serverbundleinputstream = getClass().getResourceAsStream("/server.bundle.js");

            // Pass these streams to an asset provider for the renderer
            RenderAssetProvider provider = new ResourceProvider(indexinputstream, serverbundleinputstream, StandardCharsets.UTF_8);
            // Or for a real file system: = new FilesystemProvider(new File("<index file path>"), new File("<server bundle file path>"), StandardCharsets.UTF_8);

            // Create a V8 render engine and pass it to the renderer
            V8RenderEngine v8renderengine = new V8RenderEngine();
            this.renderer = new Renderer(v8renderengine, provider);

            // Start the renderer
            renderer.startEngine();
        }

        public Future<String> renderPage(String uri) {
            // Render a request and return a resolvable future
            return renderer.renderRequest(uri);
        }
    }
}