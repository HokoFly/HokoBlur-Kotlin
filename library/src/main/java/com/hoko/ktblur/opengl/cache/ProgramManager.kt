package com.hoko.ktblur.opengl.cache

import com.hoko.ktblur.api.Mode
import com.hoko.ktblur.opengl.Program
import com.hoko.ktblur.util.getFragmentShaderCode
import com.hoko.ktblur.util.vertexShaderCode
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by yuxfzju on 2025/7/10
 */
internal object ProgramManager {
    private val programCache: MutableMap<Mode, RefCountedProgram> =
        ConcurrentHashMap<Mode, RefCountedProgram>()

    fun getProgram(mode: Mode): Program {
        synchronized(this) {
            var refCountedProgram: RefCountedProgram? = programCache[mode]
            if (refCountedProgram != null) {
                if (refCountedProgram.incrementRefCount()) {
                    return refCountedProgram.program
                } else {
                    // invalid program
                    refCountedProgram.clearRefCount()
                    programCache.remove(mode)
                }
            }

            refCountedProgram = RefCountedProgram(
                vertexShaderCode,
                getFragmentShaderCode(mode)
            )
            programCache.put(mode, refCountedProgram)
            return refCountedProgram.program
        }
    }

    fun releaseProgram(program: Program?) {
        if (program == null) {
            return
        }
        synchronized(this) {
            val it: MutableIterator<MutableMap.MutableEntry<Mode, RefCountedProgram>> =
                programCache.entries.iterator()
            while (it.hasNext()) {
                val refCountedProgram: RefCountedProgram = it.next().value
                if (refCountedProgram.program === program) {
                    refCountedProgram.decrementRefCount()
                    if (refCountedProgram.refCount <= 0) {
                        it.remove()
                        return
                    }
                }
            }
        }
        program.delete()
    }

}

internal class RefCountedProgram(vertexShaderCode: String, fragmentShaderCode: String) {
    val program: Program = Program.of(vertexShaderCode, fragmentShaderCode)
    var refCount: Int = 1
        private set

    @Volatile
    private var deleted = false

    val isInvalid: Boolean
        get() = deleted || program.id == 0

    fun incrementRefCount(): Boolean {
        synchronized(this) {
            if (this.isInvalid) {
                return false
            }
            refCount++
        }
        return true
    }

    fun decrementRefCount() {
        synchronized(this) {
            refCount--
            if (refCount <= 0) {
                deleted = true
                program.delete()
            }
        }
    }

    fun clearRefCount() {
        synchronized(this) {
            refCount = 0
            deleted = true
            program.delete()
        }
    }

}