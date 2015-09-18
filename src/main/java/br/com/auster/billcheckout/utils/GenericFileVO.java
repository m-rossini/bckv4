/*
 *
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 06/03/2007
 *
 * @(#)GenericFileVO.java 06/03/2007
 */
package br.com.auster.billcheckout.utils;

/**
 * The class <code>GenericFileVO</code> it is responsible to transport a data
 * of the web_bundlefile.
 *
 * @author Gilberto Brandão
 * @version $Id$
 * @since JDK1.4
 */
public class GenericFileVO {

    /**
     * Create a new instance of the class <code>GenericFileVO</code>.
     */
    public GenericFileVO() {
        // DOES NOTHING
    }
    
    
    /** 
     * Used to store the values of  <code>fileId</code>.
     */
    private long fileId;
    
    /** 
     * Used to store the values of  <code>path</code>.
     */
    private String path;

    /**
     * Return the value of a attribute<code>fileId</code>.
     * @return return the value of <code>fileId</code>.
     */
    public long getFileId() {
        return fileId;
    }

    /**
     * Set a value of <code>fileId</code>.
     * @param fileId
     */
    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    /**
     * Return the value of a attribute<code>path</code>.
     * @return return the value of <code>path</code>.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set a value of <code>path</code>.
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    
    
    
}
