/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.zold.api;

import java.io.IOException;
import java.nio.file.Path;
import org.cactoos.iterable.Mapped;
import org.cactoos.iterable.Skipped;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.CheckedScalar;
import org.cactoos.text.SplitText;
import org.cactoos.text.TextOf;

/**
 * Wallet.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@SuppressWarnings("PMD.ShortMethodName")
public interface Wallet {
    /**
     * This wallet's ID: an unsigned 64-bit integer.
     * @return This wallet's id
     * @throws IOException If an IO error occurs
     * @checkstyle MethodName (2 lines)
     */
    long id() throws IOException;

    /**
     * Make a payment.
     * @param amt Amount to pay in zents
     * @param bnf Wallet ID of beneficiary
     */
    void pay(long amt, long bnf);

    /**
     * Merge both {@code this} and {@code other}. Fails if they are not the
     * same wallet, as identified by their {@link #id() id}.
     * @param other Other wallet
     * @return The merged wallet
     */
    Wallet merge(Wallet other);

    /**
     * This wallet's ledger.
     * @return This wallet's ledger
     */
    Iterable<Transaction> ledger();

    /**
     * Default File implementation.
     * @checkstyle ClassDataAbstractionCouplingCheck (2 lines)
     */
    class File implements Wallet {

        /**
         * Path of this wallet.
         */
        private final Path path;

        /**
         * Ctor.
         * @param path Path of wallet
         */
        File(final Path path) {
            this.path = path;
        }

        @Override
        public long id() throws IOException {
            return new CheckedScalar<>(
                () -> Long.parseUnsignedLong(
                    new ListOf<>(
                        new SplitText(
                            new TextOf(this.path),
                            "\n"
                        )
                    ).get(2).asString(),
                    // @checkstyle MagicNumber (1 line)
                    16
                ),
                e -> new IOException(e)
            ).value();
        }

        // @todo #15:30min Implement pay method. This should add a transaction
        //  to the wallet containing the correct details with the help of
        //  RtTransaction class. Also add a unit test to replace
        //  WalletTest.payIsNotYetImplemented().
        @Override
        public void pay(final long amt, final long bnf) {
            throw new UnsupportedOperationException("pay() not yet supported");
        }

        // @todo #6:30min Implement merge method. This should merge this wallet
        //  with a copy of the same wallet. It should throw an error if a
        //  wallet is provided. Also add a unit test to replace
        //  WalletTest.mergeIsNotYetImplemented().
        @Override
        public Wallet merge(final Wallet other) {
            throw new UnsupportedOperationException(
                "merge() not yet supported"
            );
        }

        @Override
        public Iterable<Transaction> ledger() {
            return new Mapped<>(
                txt -> new RtTransaction(txt.asString()),
                new Skipped<>(
                    new ListOf<>(
                        new SplitText(
                            new TextOf(this.path),
                            "\\n"
                        )
                    ),
                    // @checkstyle MagicNumberCheck (1 line)
                    5
                )
            );
        }
    }
}
