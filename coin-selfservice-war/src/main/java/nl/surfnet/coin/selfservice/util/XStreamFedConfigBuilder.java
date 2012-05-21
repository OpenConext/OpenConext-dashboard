/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.selfservice.util;

import javax.xml.stream.XMLOutputFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import javanet.staxutils.StaxUtilsXMLOutputFactory;
import nl.surfnet.coin.selfservice.domain.FederatieConfig;

/**
 * Utility class for the {@link XStream} configuration needed to handle {@link FederatieConfig}
 */
public final class XStreamFedConfigBuilder {

  private XStreamFedConfigBuilder() {
  }

  /**
   * Creates and returns an {@link XStream} configuration for the SURFfederatie config file.
   * Output will not be indented.
   *
   * @return {@link XStream}
   */
  public static XStream getXStreamForFedConfig() {
    return XStreamFedConfigBuilder.getXStreamForFedConfig(false);
  }

  /**
   * Creates and returns an {@link XStream} configuration for the SURFfederatie config file
   *
   * @param indentOutput if set to {@literal true} the printed XML is nicely indented for the human reader
   * @return {@link XStream}
   */
  public static XStream getXStreamForFedConfig(final boolean indentOutput) {
    StaxDriver staxDriver = createHumanReadableStaxDriver(indentOutput);
    XStream xStream = createLenientXStream(staxDriver);
    xStream.autodetectAnnotations(true);
    // only alias that is not picked up by the annotation
    xStream.alias("Config", FederatieConfig.class);
    xStream.setMode(XStream.NO_REFERENCES);

    return xStream;
  }

  private static StaxDriver createHumanReadableStaxDriver(final boolean indentOutput) {
    QNameMap qNameMap = new QNameMap();
    qNameMap.setDefaultNamespace("http://www.surffederatie.nl/namespaces/fedcfg");

    return new StaxDriver(qNameMap) {
      @Override
      public XMLOutputFactory getOutputFactory() {
        if (outputFactory == null) {
          XMLOutputFactory factory = new
              StaxUtilsXMLOutputFactory(super.getOutputFactory());
          factory.setProperty(StaxUtilsXMLOutputFactory.INDENTING,
              indentOutput);
          outputFactory = factory;
        }
        return outputFactory;
      }

      protected XMLOutputFactory outputFactory = null;

    };
  }

  private static XStream createLenientXStream(final StaxDriver staxDriver) {
    return new XStream(staxDriver) {
      // Ignore unknown fields in the XML, taken from http://pvoss.wordpress.com/2009/01/08/xstream/
      @Override
      protected MapperWrapper wrapMapper(MapperWrapper next) {
        return new MapperWrapper(next) {
          @Override
          public boolean shouldSerializeMember(Class definedIn, String fieldName) {
            if (definedIn == Object.class) {
              return false;
            }
            return super.shouldSerializeMember(definedIn, fieldName);
          }
        };
      }
    };
  }
}
