// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import webit.script.Engine;
import webit.script.io.Buffers;
import webit.script.io.charset.CoderFactory;
import webit.script.io.charset.Decoder;
import webit.script.io.charset.Encoder;

/**
 *
 * @author zqq90
 */
public class DefaultCoderFactory implements CoderFactory {

    @Override
    public Encoder newEncoder(final String encoding) {
        return this.newEncoder(encoding, Buffers.getNormalPeers());
    }

    @Override
    public Decoder newDecoder(final String encoding) {
        return this.newDecoder(encoding, Buffers.getMiniPeers());
    }

    @Override
    public Encoder newEncoder(String encoding, Buffers buffers) {
        if (encoding == Engine.UTF_8) {
            return new UTF8Encoder(buffers);
        } else {
            return new DefaultEncoder(encoding, buffers);
        }
    }

    @Override
    public Decoder newDecoder(String encoding, Buffers buffers) {
        if (encoding == Engine.UTF_8) {
            return new UTF8Decoder(buffers);
        } else {
            return new DefaultDecoder(encoding, buffers);
        }
    }
}
