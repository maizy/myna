/**
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

@TypeDef(
    name = "jsonb", typeClass = JsonBinaryType.class
)
package dev.maizy.myna.db;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.TypeDef;
