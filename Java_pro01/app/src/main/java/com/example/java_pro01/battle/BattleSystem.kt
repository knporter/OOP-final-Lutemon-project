package com.example.java_pro01.battle

import com.example.java_pro01.models.Lutemon
import kotlin.math.max
import kotlin.random.Random

/**
 * Battle System - Handles turn-based combat between Lutemons
 */
class BattleSystem(
    private val lutemon1: Lutemon,
    private val lutemon2: Lutemon,
    private val battleLog: MutableList<String> = mutableListOf()
) {
    private var currentTurn = 1
    private var isLutemon1Turn = true

    fun executeTurn(action: String): Boolean {
        // 如果不是第一回合，添加分隔线
        if (currentTurn > 1) {
            battleLog.add("----------------------------------------")
        }

        // 记录当前回合
        val attacker = if (isLutemon1Turn) lutemon1 else lutemon2
        val defender = if (isLutemon1Turn) lutemon2 else lutemon1
        battleLog.add("Turn $currentTurn")

        when (action) {
            "attack" -> {
                // 计算基础伤害 = 攻击者的ATK - (防御者的DEF / 2)
                val baseDamage = attacker.getAttack() - (defender.getDefense() / 2)
                
                // 计算最终伤害 = max(基础伤害, 1)
                val finalDamage = max(baseDamage, 1)

                // Check for critical hit (Adjusted chance to 35%)
                if (Random.nextFloat() < 0.35) {
                    attacker.takeDamage(7)
                    battleLog.add("${attacker.name} loses 7 health points [RED]Critical attack[/RED]")
                } else {
                    // 应用普通伤害到攻击者
                    attacker.takeDamage(finalDamage)
                    battleLog.add("${attacker.name} loses $finalDamage health points")
                }

                // 检查战斗是否结束
                if (!attacker.isAlive()) {
                    // 记录战斗结果
                    defender.recordBattle(true)
                    attacker.recordBattle(false)
                    
                    // 胜利者获得1点经验值，失败者失去1点经验值（如果有的话）
                    defender.addExperience(1)
                    if (attacker.getExperience() > 0) {
                        attacker.addExperience(-1)
                    }
                    

                    return true
                }
            }
            "heal" -> {
                val healAmount = 2 // 固定恢复量
                val oldHealth = defender.currentHealth
                defender.heal(healAmount)
                
                if (oldHealth < defender.currentHealth) {
                    // Simplified heal log message (English)
                    battleLog.add("${defender.name} recovers ${defender.currentHealth - oldHealth} health points")
                } else {
                    battleLog.add("${defender.name} recovers 0 health points")
                }
            }
            else -> throw IllegalArgumentException("Invalid action: $action")
        }

        // 切换回合
        isLutemon1Turn = !isLutemon1Turn
        currentTurn++
        return false
    }

    fun getBattleLog(): List<String> = battleLog.toList()

    fun getCurrentTurn() = currentTurn

    fun isLutemon1Turn() = isLutemon1Turn

    fun getCurrentAttacker() = if (isLutemon1Turn) lutemon1 else lutemon2

    fun getCurrentDefender() = if (isLutemon1Turn) lutemon2 else lutemon1
}