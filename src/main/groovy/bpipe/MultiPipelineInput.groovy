/*
 * Copyright (c) Murdoch Childrens Research Institute and Contributers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package bpipe

import java.util.Iterator;
import groovy.util.logging.Log;

/**
 * Extends {@link PipelineInput} to handle multiple inputs.
 * <p>
 * Where {@link PipelineInput} tries to always return just a single
 * input that matches what the user expects, {@link MultiPipelineInput} 
 * resolves all available inputs that match what the user expects and returns
 * them separated by spaces.
 * 
 * @author ssadedin
 */
@Log
class MultiPipelineInput extends PipelineInput implements Iterable {
    
    MultiPipelineInput(def input, List<PipelineStage> stages) {
		super(input,stages)	
	}
	
	/**
	 * Maps to all of the values separated by spaces
	 */
    @Override
	public String mapToCommandValue(def values) {
		def result = Utils.box(values)
        result.each { this.resolvedInputs << String.valueOf(it) }   
        return result.collect { String.valueOf(it) }.join(' ')
	}

	String toString() {
       List boxed = Utils.box(super.@input).unique()
       this.resolvedInputs += boxed
       return boxed.join(" ")
    }
    
    def propertyMissing(String name) {
        
        if(this.resolvedInputs)
            this.resolvedInputs.clear()
            
        def result = super.propertyMissing(name)
        if(result) {
            def mp = new MultiPipelineInput(this.resolvedInputs.clone(), stages)
            mp.parent = this
            mp.resolvedInputs = this.resolvedInputs
            log.info("My resolved inputs: " + this.resolvedInputs.hashCode() + " child resolved inputs " + mp.resolvedInputs.hashCode())
            return mp
        }
     }
    
	@Override
	public Iterator iterator() {
		return Utils.box(super.@input).listIterator()
	}
}
