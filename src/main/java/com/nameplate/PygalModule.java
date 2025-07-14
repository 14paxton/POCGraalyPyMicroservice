package com.nameplate;

import io.micronaut.graal.graalpy.annotations.GraalPyModule;

@GraalPyModule("pygal")
interface PygalModule {
    StackedBar StackedBar();

    interface StackedBar {
        void add(String title, int[] i);

        Svg render();
    }

    interface Svg {
        String decode();
    }
}