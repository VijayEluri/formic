/* class JComponent
 *
 * Copyright (C) 2001  R M Pitman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package charvax.swing;

import java.util.HashMap;
import java.util.Map;

import charva.awt.Component;
import charva.awt.Insets;
import charva.awt.Point;

import charvax.swing.border.Border;

/**
 * The base class for charva.swing components. JComponent provides a
 * border property that implicitly defines the component's insets.
 * Note that JComponent is a subclass of Component, not of Container
 * (whereas java.swing.JComponent is a subclass of java.awt.Container).
 */
public abstract class JComponent
        extends Component {

    public JComponent() {
    }

    public void setBorder(Border border_) {
        _border = border_;
    }

    public Border getBorder() {
        return _border;
    }

    public Insets getInsets() {
        if (_border != null) {
            return _border.getBorderInsets(this);
        } else
            return new Insets(0, 0, 0, 0);
    }


    /**
     * Draws the border of the component (if there is one).
     */
    public void draw() {
        Point origin = getLocationOnScreen();

//	int colorpair = getCursesColor();

        /* Blank out the area of this component, but only if this
         * component's color-pair is different than that of the
         * parent container.
        Container parent = getParent();
        if (parent != null && colorpair != parent.getCursesColor())
            Toolkit.getDefaultToolkit().blankBox(origin, this.getSize(), colorpair);
         */

        if (_border != null) {
            _border.paintBorder(this, 0,
                    origin.x, origin.y,
                    this.getWidth(), this.getHeight());
        }
    }

    /**
     * @see javax.swing.JComponent#getClientProperty(Object)
     */
    public Object getClientProperty(Object key) {
        synchronized(_clientProperties){
          return _clientProperties != null ? _clientProperties.get(key) : null;
        }
    }

    /**
     * @see javax.swing.JComponent#putClientProperty(Object,Object)
     */
    public void putClientProperty(Object key, Object value) {
        if(_clientProperties == null){
            _clientProperties = new HashMap();
        }

        Object old = null;
        synchronized(_clientProperties) {
            old = _clientProperties.get(key);
            if(value != null) {
                _clientProperties.put(key, value);
            }else if (old != null) {
                _clientProperties.remove(key);
            }else {
                return;
            }
        }
        firePropertyChange(key.toString(), old, value);
    }

    //====================================================================
    // INSTANCE VARIABLES

    protected Border _border = null;

    private Map _clientProperties;
}
