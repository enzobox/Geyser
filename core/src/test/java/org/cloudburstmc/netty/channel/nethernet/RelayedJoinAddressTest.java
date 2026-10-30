/*
 * Copyright (c) 2026 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.cloudburstmc.netty.channel.nethernet;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * BoxMC BUG-0686 guard: NetherNetChannelInitialiser#preInitChannel reads remoteAddress() before ICE connects, which is
 * the signaling peer. Behind a signaling relay (MCXboxBroadcast build 156+) that is the relay for every player. The
 * channel must report the selected ICE pair once it is set, or Floodgate forwards the relay's address. Fails with
 * network 2.0.0.CR6-20261026.130552-16 and passes from -20261027.222103-17 (CloudburstMC/Network#105).
 */
class RelayedJoinAddressTest {
    private static final InetSocketAddress RELAY = new InetSocketAddress("192.0.2.1", 19132);
    private static final InetSocketAddress PLAYER = new InetSocketAddress("203.0.113.7", 54321);

    @Test
    void selectedPairReplacesTheRelayAddressAfterAnEarlyRead() {
        NetherNetChildChannel child = new NetherNetChildChannel(null, null, RELAY, RELAY);
        // What the initializer sees before ICE connects
        assertEquals(RELAY, child.remoteAddress());

        // What NetherNetServerChannel does on RTC_CONNECTED with the selected pair
        child.setRemoteAddress(PLAYER);
        assertEquals(PLAYER, child.remoteAddress());
    }
}
